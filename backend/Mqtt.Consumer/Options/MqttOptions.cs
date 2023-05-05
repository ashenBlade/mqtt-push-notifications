using System.ComponentModel.DataAnnotations;
using System.Net;

namespace Mqtt.Consumer.Options;

public class MqttOptions
{
    public const string SectionName = "Mqtt";
    
    [Required]
    public string Host { get; set; } = null!;

    [Required]
    public int Port { get; set; }

    [Required]
    public string Topic { get; set; } = null!;
}