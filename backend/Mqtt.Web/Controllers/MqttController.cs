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

    [HttpPost("message/single")]
    public async Task<IActionResult> PostSingleMessage(Guid deviceId, string title = "Sample title", string body = "Hello, world!", PushImportance importance = PushImportance.None)
    {
        var topic = $"/push/{deviceId}";
        var message = await SendMessageAsync(title, body, importance, topic);
        return Ok(message);
    }

    private async Task<Message> SendMessageAsync(string title, string body, PushImportance importance, string topic)
    {
        var message = new Message(title, body, importance);
        _logger.LogInformation("Сообщение отправляется в топик {Topic}", topic);
        var result = await _client.PublishStringAsync(topic, JsonSerializer.Serialize(message),
                         MqttQualityOfServiceLevel.AtLeastOnce);
        _logger.LogInformation("Сообщение отправлено в топик {Topic}", topic);
        _logger.LogDebug("Успшено: {IsSuccess}. Код: {ReasonCode}", result.IsSuccess, result.ReasonCode);
        return message;
    }

    [HttpPost("message/all")]
    public async Task<IActionResult> PostMessageAll(string title = "Sample title", string body = "Hello, world!", PushImportance importance = PushImportance.None)
    {
        var topic = "/push";
        var message = await SendMessageAsync(title, body, importance, topic);
        return Ok(message);
    }
}