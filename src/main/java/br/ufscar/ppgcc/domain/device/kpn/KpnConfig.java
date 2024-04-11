package br.ufscar.ppgcc.domain.device.kpn;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableConfigurationProperties({ KpnConfig.KpnGripProperties.class, KpnConfig.KpnMqttOptions.class })
class KpnConfig {

    @ConfigurationProperties(prefix = "kpn.grip")
    record KpnGripProperties(String applicationId, String clientId, String clientSecret) {
    }

    @ConfigurationProperties(prefix = "kpn.things.mqtt")
    record KpnMqttOptions(String url, String topic) {
    }

    @Bean
    KpnTokenRequest kpnTokenRequest(KpnGripProperties properties) {
        return new KpnTokenRequest(properties.applicationId(), properties.clientId(), properties.clientSecret());
    }

    @Bean
    MessageChannel kpnMqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    MessageProducer kpnMqttInbound(KpnMqttOptions kpnMqttOptions) {
        final var adapter = new MqttPahoMessageDrivenChannelAdapter(kpnMqttOptions.url(), "kpnClient", kpnMqttOptions.topic());
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setOutputChannel(kpnMqttInputChannel());
        return adapter;
    }

}
