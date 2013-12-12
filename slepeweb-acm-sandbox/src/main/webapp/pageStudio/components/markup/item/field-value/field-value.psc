<?xml version="1.0"?>
<!DOCTYPE ps-component PUBLIC "-//Mediasurface//DTD Page Studio Components//EN" "">
<ps-component id="fieldvaluemc" componentType="configurable" category="Item" template="false" autoConfigure="true" markup="true">
	<configurable>
		<name>Field Value</name>
		<description>Prints the value of a given field.</description>
		<location>field-value.jsp</location>
		<icon>field-value-icon.png</icon>
		<properties>
			<property name='name' type='string' mandatory="true" />
			<property name='maxlength' type='numeric' defaultValue='0'/>
			<property name='url' type='string' />
		</properties>
	</configurable>
</ps-component>