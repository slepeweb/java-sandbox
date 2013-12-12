<?xml version="1.0"?>
<!DOCTYPE ps-component PUBLIC "-//Mediasurface//DTD Page Studio Components//EN" "">
<ps-component id="ext1" componentType="configurable" category="Example" autoConfigure="true">
	<configurable>
		<name>Validator Test</name>
		<description>Displays a range of inputs</description>
		<location>validator-test.jsp</location>
		<icon>validator-test-icon.png</icon>
		<properties>
			<property name='string1' type='string' size='25' defaultValue='a string property' />
			<property name='numeric1' type='numeric' size='300' mandatory='true' />
			<property name='singlevaluelist1' type='singlevaluelist' range='value1|value2' />
			<property name='list1' type='list' range='/l1|/l2|/l3|/l4' mandatory='true' />
		</properties>
	</configurable>
</ps-component>