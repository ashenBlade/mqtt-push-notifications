namespace Mqtt.Models;

/// <summary>
/// Пуш, передаваемый на андроид-клиент
/// </summary>
public class Message
{
    public Guid Id { get; set; }
    public string Title { get; set; }
    public string Body { get; set; }
    public string Template { get; set; }
}