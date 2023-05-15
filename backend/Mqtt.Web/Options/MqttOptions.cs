using System.ComponentModel.DataAnnotations;

namespace Mqtt.Web.Options;

public class MqttOptions
{
    public const string SectionName = "Mqtt";
    
    [Required]
    public string Host { get; set; } = null!;

    [Required]
    public int Port { get; set; }
}