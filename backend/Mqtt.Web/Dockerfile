FROM mcr.microsoft.com/dotnet/aspnet:6.0 AS base
WORKDIR /app

FROM mcr.microsoft.com/dotnet/sdk:6.0 AS build
WORKDIR /src
COPY ["Mqtt.Web/Mqtt.Web.csproj", "Mqtt.Web/"]
COPY ["Mqtt.Models/Mqtt.Models.csproj", "Mqtt.Models/"]
RUN dotnet restore "Mqtt.Web/Mqtt.Web.csproj"
COPY . .
WORKDIR "/src/Mqtt.Web"
RUN dotnet build "Mqtt.Web.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "Mqtt.Web.csproj" -c Release -o /app/publish /p:UseAppHost=false

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "Mqtt.Web.dll"]
