using Microsoft.AspNetCore.Mvc;

namespace Mqtt.Web.Controllers;

[ApiController]
[Route("[controller]")]
public class AndroidController : ControllerBase
{
    private readonly ILogger<AndroidController> _logger;

    public AndroidController(ILogger<AndroidController> logger)
    {
        _logger = logger;
    }

    /// <summary>
    /// Зарегистировать устройство в системе для получения уведомлений на него
    /// </summary>
    /// <returns>Объект с ID зарегистрированного устройства</returns>
    [HttpPost("subscribe")]
    public IActionResult AcquireDeviceId()
    {
        var deviceId = Guid.NewGuid();
        _logger.LogInformation("Зарегистрировано новое устройство: {DeviceId}", deviceId);
        return Ok(new
        {
            deviceId
        });
    }
}