namespace Mqtt.Models;

/// <summary>
/// Пуш, передаваемый на андроид-клиент
/// </summary>
public record Message(string Title, string Body, PushImportance PushImportance)
{
    public Guid Id { get; init; } = Guid.NewGuid();
}