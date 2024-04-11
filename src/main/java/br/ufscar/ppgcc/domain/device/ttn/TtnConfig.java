package br.ufscar.ppgcc.domain.device.ttn;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableConfigurationProperties(TtnConfig.TtnMqttOptions.class)
public class TtnConfig {

    @ConfigurationProperties("ttn.stack.mqtt")
    record TtnMqttOptions(String url, String username, String password, String topic) {
    }

    @Bean
    MqttPahoClientFactory ttnClientFactory(TtnMqttOptions ttnMqttOptions) {
        var factory = new DefaultMqttPahoClientFactory();
        var options = new MqttConnectOptions();
        options.setServerURIs(new String[] { ttnMqttOptions.url()});
        options.setUserName(ttnMqttOptions.username());
        options.setPassword(ttnMqttOptions.password().toCharArray());
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    MessageChannel ttnMqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    MessageProducer ttnMqttInbound(TtnMqttOptions ttnMqttOptions, MqttPahoClientFactory ttnClientFactory) {
        final var adapter = new MqttPahoMessageDrivenChannelAdapter("ttnClient", ttnClientFactory, ttnMqttOptions.topic());
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setOutputChannel(ttnMqttInputChannel());
        return adapter;
    }

}
