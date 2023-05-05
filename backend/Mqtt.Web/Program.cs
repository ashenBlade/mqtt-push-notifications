using Microsoft.Extensions.Options;
using Mqtt.Web.Options;
using MQTTnet;
using MQTTnet.Client;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

builder.Services.AddControllers();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
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

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();

app.Run();