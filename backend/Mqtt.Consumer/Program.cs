using Mqtt.Consumer;
using Mqtt.Consumer.Options;
using MQTTnet;

IHost host = Host.CreateDefaultBuilder(args)
                 .ConfigureServices((ctx, services) =>
                  {
                      services.AddOptions<MqttOptions>()
                              .Bind(ctx.Configuration.GetRequiredSection(MqttOptions.SectionName));
                      services.AddSingleton(_ => new MqttFactory());
                      services.AddHostedService<TopicConsumerBackgroundService>();
                  })
                 .Build();

await host.RunAsync();