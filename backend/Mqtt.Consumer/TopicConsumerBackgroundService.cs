using Microsoft.Extensions.Options;
using Mqtt.Consumer.Options;
using MQTTnet;
using MQTTnet.Client;
using MQTTnet.Extensions.ManagedClient;
using MQTTnet.Protocol;

namespace Mqtt.Consumer;

public class TopicConsumerBackgroundService : BackgroundService
{
    private readonly MqttFactory _factory;
    private readonly IOptionsMonitor<MqttOptions> _options;
    private readonly ILogger<TopicConsumerBackgroundService> _logger;

    public TopicConsumerBackgroundService(MqttFactory factory, IOptionsMonitor<MqttOptions> options, ILogger<TopicConsumerBackgroundService> logger)
    {
        _factory = factory;
        _options = options;
        _logger = logger;
    }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        await Task.Yield();
        _logger.LogInformation("Создание клиента MQTT");
        var client = _factory.CreateManagedMqttClient();
        
        _logger.LogInformation("Регистрирование обработчиков");
        
        var topic = _options.CurrentValue.Topic;
        client.ConnectingFailedAsync += args =>
        {
            _logger.LogError(args.Exception, "Ошибка во время подключения. Причина: {Reason}", args.ConnectResult.ReasonString);
            return Task.CompletedTask;
        };
        client.ConnectionStateChangedAsync += args =>
        {
            _logger.LogInformation("Состояние подключения изменилось");
            return Task.CompletedTask;
        };
        client.ConnectedAsync += args =>
        {
            _logger.LogInformation("Клиент подключился");
            return Task.CompletedTask;
        };
        client.ApplicationMessageReceivedAsync += args =>
        {
            var message = args.ApplicationMessage;
            var convertPayloadToString = BitConverter.ToInt32( message.PayloadSegment );
            _logger.LogInformation("Получено сообщение. Нагрузка: {Message}. Content-Type: {ContentType}. Topic: {Topic}", convertPayloadToString, message.ContentType, message.Topic);
            return Task.CompletedTask;
        };

        client.ApplicationMessageSkippedAsync += args =>
        {
            var message = args.ApplicationMessage.ApplicationMessage;
            _logger.LogInformation("Пропущено сообщение. Нагрузка: {Message}. Topic: {Topic}", message.ConvertPayloadToString(), message.Topic);
            return Task.CompletedTask;
        };
        
        client.ApplicationMessageProcessedAsync += args =>
        {
            var message = args.ApplicationMessage.ApplicationMessage;
            _logger.LogInformation("Сообщение обработано. Нагрузка: {Message}. Topic: {Topic}", message.ConvertPayloadToString(), message.Topic);
            return Task.CompletedTask;
        };
        
        var mqttClientOptions = new MqttClientOptionsBuilder()
                               .WithTcpServer(_options.CurrentValue.Host, _options.CurrentValue.Port)
                               .Build();

        var managedMqttClientOptions = new ManagedMqttClientOptionsBuilder()
                                      .WithClientOptions(mqttClientOptions)
                                      .Build();
        
        _logger.LogInformation("Старт MQTT клиента");
        await client.StartAsync(managedMqttClientOptions);
        _logger.LogInformation("Подписка на топик {Topic}", topic);
        await client.SubscribeAsync(topic, MqttQualityOfServiceLevel.AtLeastOnce);
        
        _logger.LogInformation("MQTT клиент стартовал");
        var tcs = new TaskCompletionSource();
        await using var _ = stoppingToken.Register(o => ( ( TaskCompletionSource ) o! ).SetResult(), tcs);
        
        try
        {
            await tcs.Task;
        }
        catch (TaskCanceledException e)
        {
            _logger.LogInformation(e, "Запрошено завершение работы");
        }
        _logger.LogInformation("Завершение работы MQTT клиента");
        await client.StopAsync();
        _logger.LogInformation("Работа MQTT клиента завершена");
    }
}