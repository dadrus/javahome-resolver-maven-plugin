# javahome-resolver-maven-plugin
**Performs the lookup of the path to the used jdk and exports it as maven property**

If you have a need to develop and maintain a system and your maintenance and development branches diverge in used JDK, you could use [Maven Toolchains Plugin](https://maven.apache.org/plugins/maven-toolchains-plugin/).
But what if some of your tests require the path to the JDK used to build the system, e.g. to start an application server which can't be started with a newer
JDK, then this plugin may be for you.

In my specific case I use [arquillian](http://arquillian.org) with JBoss. The corresponding arquillian container [configuration](https://docs.jboss.org/author/display/ARQ/JBoss+AS+7.1,+JBoss+EAP+6.0+-+Managed)
expects an optional `javaHome` variable provided, otherwise a system wide `JAVA_HOME` is used. Given the situation, that the development main stream uses java
8 and JBoss EAP 6.4, but the maintenance must be performed using java 7 and JBoss EAP 6.0 (which does not support java 8), I was looking for a way not to be
forced to switch between environment configurations if I just switch a branch. The idea was to use the aforementioned maven toolchains plugin. Unfortunately 
neither the toolchains plugin, nor the surefire, failsafe, or other toolchains aware plugins, I'm familiar with, do expose a variable pointing to a path for the
choosen JDK. Thus this plugin was born.

## Plugin Behaviour
- exports a `javaHome` property with the path to choosen JDK if maven toolchains plugin is used, otherwise
- exports a `javaHome` property which has the same value as the configured `JAVA_HOME` environment variable.

## Limitations
The plugin does not integrate with eclipse properly. I was unable to find out how to force eclipse to refilter affected ressources.

## Usage
You should add "javahome-resolver-maven-plugin" to your `<build>` configuration. The default lifecycle phase is `validate`. The plugin has a single goal - `resolve`.
Here is an example:

```xml
<build>
	<plugins>
		<plugin>
			<groupId>org.apache.maven.plugins</groupId>
			<artifactId>maven-toolchains-plugin</artifactId>
			<executions>
				<execution>
					<phase>validate</phase>
					<goals>
						<goal>toolchain</goal>
					</goals>
				</execution>
			</executions>
			<configuration>
				<toolchains>
					<jdk>
						<version>${maven.compiler.target}</version>
					</jdk>
				</toolchains>
			</configuration>
		</plugin>
		<plugin>
			<groupId>eu.drus.maven.plugins</groupId>
			<artifactId>javahome-resolver-maven-plugin</artifactId>
			<executions>
				<execution>
					<goals>
						<goal>resolve</goal>
					</goals>
				</execution>
			</executions>
		</plugin>
	</plugins>
</build>
```


