<?xml version="1.0"?>
<!DOCTYPE ps-component PUBLIC "-//Mediasurface//DTD Page Studio Components//EN" "">
<ps-component id="twitter" componentType="configurable" category="Social" autoConfigure="true">
	<configurable>
		<name>Twitter</name>
		<description>A simple Twitter client.</description>
		<location>twitter.jsp</location>
		<icon>images/twitter-icon.png</icon>
		<properties>
			<property name='username' type='string' size='60' mandatory='true' encrypted='true' />
			<property name='password' type='password' size='300' mandatory='true' encrypted='true' />
			<property name='count'    type='singlevaluelist' range='1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20' defaultValue='20' size='3'/>
		</properties>
	</configurable>
</ps-component>