/*
 * Copyright (c) 2019-2029, Dreamlu 卢春梦 (596392912@qq.com & www.net.dreamlu.net).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dreamlu.iot.mqtt.core.client;

import net.dreamlu.iot.mqtt.codec.*;
import net.dreamlu.iot.mqtt.core.common.MqttMessageListener;
import org.tio.client.ClientChannelContext;
import org.tio.client.TioClient;
import org.tio.core.Tio;

import java.nio.ByteBuffer;

/**
 * mqtt 客户端
 *
 * @author L.cm
 */
public final class MqttClient {
	private final TioClient tioClient;
	private final MqttClientCreator config;
	private final ClientChannelContext context;
	private final MqttClientSubManage subManage;

	public static MqttClientCreator create() {
		return new MqttClientCreator();
	}

	MqttClient(TioClient tioClient,
			   MqttClientCreator config,
			   ClientChannelContext context,
			   MqttClientSubManage subManage) {
		this.tioClient = tioClient;
		this.config = config;
		this.context = context;
		this.subManage = subManage;
	}

	/**
	 * 订阅
	 *
	 * @param topicFilter topicFilter
	 * @param listener    MqttMessageListener
	 * @return MqttClient
	 */
	public MqttClient subQos0(String topicFilter, MqttMessageListener listener) {
		// TODO L.cm 对 topicFilter 校验
		MqttSubscribeMessage message = MqttMessageBuilders.subscribe()
			.addSubscription(MqttQoS.AT_MOST_ONCE, topicFilter)
			.messageId(MqttClientMessageId.getId())
			.build();
		Tio.send(context, message);
		// 绑定 subManage listener
		return this;
	}

	/**
	 * 订阅
	 *
	 * @param topicFilter topicFilter
	 * @param listener    MqttMessageListener
	 * @return MqttClient
	 */
	public MqttClient subQos1(String topicFilter, MqttMessageListener listener) {
		// TODO L.cm 对 topicFilter 校验
		return this;
	}

	/**
	 * 订阅
	 *
	 * @param topicFilter topicFilter
	 * @param listener    MqttMessageListener
	 * @return MqttClient
	 */
	public MqttClient subQos2(String topicFilter, MqttMessageListener listener) {
		// TODO L.cm 对 topicFilter 校验
		return this;
	}

	/**
	 * 取消订阅
	 *
	 * @param topicFilter topicFilter
	 * @return MqttClient
	 */
	public MqttClient unSubscribe(String topicFilter) {
		return this;
	}

	/**
	 * 发布消息
	 *
	 * @param topic   topic
	 * @param payload 消息体
	 * @return 是否发送成功
	 */
	public Boolean publish(String topic, ByteBuffer payload) {
		return publish(topic, payload, MqttQoS.AT_MOST_ONCE);
	}

	/**
	 * 发布消息
	 *
	 * @param topic   topic
	 * @param payload 消息体
	 * @param qos     MqttQoS
	 * @return 是否发送成功
	 */
	public Boolean publish(String topic, ByteBuffer payload, MqttQoS qos) {
		return publish(topic, payload, qos, false);
	}

	/**
	 * 发布消息
	 *
	 * @param topic   topic
	 * @param payload 消息体
	 * @param retain  是否在服务器上保留消息
	 * @return 是否发送成功
	 */
	public Boolean publish(String topic, ByteBuffer payload, boolean retain) {
		return publish(topic, payload, MqttQoS.AT_MOST_ONCE, retain);
	}

	/**
	 * 发布消息
	 *
	 * @param topic   topic
	 * @param payload 消息体
	 * @param qos     MqttQoS
	 * @param retain  是否在服务器上保留消息
	 * @return 是否发送成功
	 */
	public Boolean publish(String topic, ByteBuffer payload, MqttQoS qos, boolean retain) {
		MqttPublishMessage message = MqttMessageBuilders.publish()
			.topicName(topic)
			.payload(payload)
			.qos(qos)
			.retained(retain)
			.messageId(MqttClientMessageId.getId())
			.build();
		return Tio.send(context, message);
	}

	/**
	 * 重连
	 *
	 * @throws Exception 异常
	 */
	public MqttClient reconnect() throws Exception {
		tioClient.reconnect(context, config.getTimeout());
		return this;
	}

	/**
	 * 断开 mqtt 连接
	 */
	public boolean disconnect() {
		boolean result = Tio.send(context, MqttMessage.DISCONNECT);
		if (result) {
			Tio.close(context, "MqttClient disconnect.");
		}
		return result;
	}

	/**
	 * 停止客户端
	 *
	 * @return 是否停止成功
	 */
	public boolean stop() {
		// 先断开连接
		this.disconnect();
		return tioClient.stop();
	}

	/**
	 * 获取 ClientChannelContext
	 *
	 * @return ClientChannelContext
	 */
	public ClientChannelContext getContext() {
		return context;
	}

}
