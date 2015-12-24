Certain 'static' resources are expected to be found at root level, but in order
for them to be served by the default servlet, as configured by web.xml, they must actually reside below 
/resources. See http://www.jroller.com/kenwdelong/entry/spring_default_servlets_and_serving for an
idea of where the problems lie.

As a consequence, files google92fb61be637e5ea0.html and robots.txt MUST NOT be moved from the 
resources folder - the apache virtual container is proxy-passing to specific paths.