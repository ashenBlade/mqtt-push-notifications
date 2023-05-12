using System.Text.Json;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Options;
using Mqtt.Models;
using Mqtt.Web.Options;
using MQTTnet;
using MQTTnet.Client;
using MQTTnet.Protocol;

namespace Mqtt.Web.Controllers;

[ApiController]
[Route("[controller]")]
public class MqttController : ControllerBase
{
    private readonly IMqttClient _client;
    private readonly IOptions<MqttOptions> _options;
    private readonly ILogger<MqttController> _logger;

    public MqttController(IMqttClient client, IOptions<MqttOptions> options, ILogger<MqttController> logger)
    {
        _client = client;
        _options = options;
        _logger = logger;
    }

    [HttpPost("single/integer")]
    public async Task<IActionResult> PostSingleInteger()
    {
        var i = Random.Shared.Next();
        var message = new MqttApplicationMessageBuilder()
                                    .WithPayload(BitConverter.GetBytes(i))
                                    .WithTopic(_options.Value.IntegersTopic)
                                    .Build();
        _logger.LogDebug("Сообщение создано");
        var result = await _client.PublishAsync(message);
        _logger.LogInformation("Сообщение отправлено. Результат {IsSuccess}. Reason: {ReasonString}", result.IsSuccess, result.ReasonString);
        return Ok(new
        {
            Value = i
        });
    }

    [HttpPost("single/message")]
    public async Task<IActionResult> PostSingleMessage(Guid deviceId, string title = "Sample title", string body = "Hello, world!", PushImportance importance = PushImportance.None)
    {
        var message = new Message(title, body, importance);
        _logger.LogDebug("Сообщение создано");
        var topic = $"/push/{deviceId}";
        _logger.LogInformation("Сообщение отправляется в топик {Topic}", topic);
        var result = await _client.PublishStringAsync(topic, JsonSerializer.Serialize(message),
                         MqttQualityOfServiceLevel.AtLeastOnce);
        _logger.LogInformation("Сообщение отправлено в топик {Topic}", topic);
        _logger.LogDebug("Успшено: {IsSuccess}. Код: {ReasonCode}", result.IsSuccess, result.ReasonCode);
        return Ok(message);
    }
}