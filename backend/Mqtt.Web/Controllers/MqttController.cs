using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Options;
using Mqtt.Web.Options;
using MQTTnet;
using MQTTnet.Client;

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
                                    .WithTopic(_options.Value.Topic)
                                    .Build();
        _logger.LogDebug("Сообщение создано");
        var result = await _client.PublishAsync(message);
        _logger.LogInformation("Сообщение отправлено. Результат {IsSuccess}. Reason: {ReasonString}", result.IsSuccess, result.ReasonString);
        return Ok(new
        {
            Value = i
        });
    }
}