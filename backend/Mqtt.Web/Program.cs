using Microsoft.Extensions.Options;
using Mqtt.Web.Options;
using MQTTnet;
using MQTTnet.Client;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();
builder.Services.AddSingleton(_ => new MqttFactory());
builder.Services
       .AddOptions<MqttOptions>()
       .Bind(builder.Configuration.GetRequiredSection(MqttOptions.SectionName))
       .ValidateDataAnnotations()
       .ValidateOnStart();

builder.Services.AddScoped<IMqttClient>(sp =>
{
    var client = sp.GetRequiredService<MqttFactory>()
                   .CreateMqttClient();
    var options = sp.GetRequiredService<IOptions<MqttOptions>>().Value;
    client.ConnectAsync(new MqttClientOptionsBuilder()
                       .WithTcpServer(options.Host, options.Port)
                       .Build())
          .GetAwaiter()
          .GetResult();
    return client;
});

var app = builder.Build();

app.UseHttpsRedirection();

app.UseSwagger();
app.UseSwaggerUI();

app.MapControllers();

app.Run();