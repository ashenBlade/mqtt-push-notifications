namespace Mqtt.Models;

/// <summary>
/// Пуш, передаваемый на андроид-клиент
/// </summary>
public record Message(string Title, string Body, string Template)
{
    public Guid Id { get; init; } = Guid.NewGuid();
}