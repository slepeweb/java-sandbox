<?xml version="1.0"?>
<!DOCTYPE ps-component PUBLIC "-//Mediasurface//DTD Page Studio Components//EN" "">
<ps-component id="cc2" componentType="configurable" category="Example">
	<configurable>
		<name>Custom User Details</name>
		<description>Displays the configured user details</description>
		<location>user-details.jsp</location>
		<configurator>user-details-cfg.jsp</configurator>
		<icon>user-details-icon.png</icon>
		<properties>
			<property name='name' type='string' size='100' />
			<property name='sex' type='singlevaluelist' defaultValue='male' range='male|female' />
			<property name='age' type='numeric' defaultValue='100'  />
		</properties>
	</configurable>
</ps-component>