<?xml version="1.0"?>
<!DOCTYPE ps-component PUBLIC "-//Mediasurface//DTD Page Studio Components//EN" "">
<ps-component id="extPSImage" componentType="configurable" category="Example">
    <configurable>
        <name>External Image</name>
        <description>Displays an image from a selection</description>
        <location>external-image.jsp</location>
        <icon>external-image-icon.png</icon>
        <configurator>external-image-cfg.jsp</configurator>
        <properties>
            <property name='alt' type='string' size='100' />
            <property name='selection' type='singlevaluelist' range='/pageStudio/components/examples/external-image/cubes_red.png|/pageStudio/components/examples/external-image/cubes_blue.png' />
        </properties>
    </configurable>
</ps-component>