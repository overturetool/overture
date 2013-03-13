Before you download and try to use the code provided in this
open-source repository, please read the development FAQ first!

http://www.overturetool.org/twiki/bin/view/Main/DevFaQ

Required settings in Settings.xml

      <profile>
        <id>default</id>
        <activation>
         
          <activeByDefault>true</activeByDefault>
        </activation>
        <properties>
          <user.vdmtoolscmdpath>c:\Program Files\The VDM++ Toolbox v8.2b\bin\vppde.exe</user.vdmtoolscmdpath>
        </properties>

      </profile>

In order to use the plugin in a project two goals exist:

type: Run VDM Tools type check on the project

code: Run VDM Tools Java Code gen

Ex:

<build>
		<plugins>
			<plugin>
				<groupId>org.overture.tools</groupId>
				<artifactId>vdmt</artifactId>
				<configuration>
					<excludePackages>
						<param>org.overture.traces.test
						</param>
						<param>org.overture.traces.VDMUnit
						</param>
					</excludePackages>
					<importPackages>
						<param>org.overture.ast.itf</param>
						<param>org.overture.ast.imp</param>
					</importPackages>
				</configuration>
				<executions>
				<!-- 	<execution>
						<phase>process-resources</phase>
						<goals>
							<goal>type</goal>
						</goals>
					</execution> -->
				</executions>
			</plugin>
		</plugins>
	</build>
