/*

Copyright (c) 2012, Jirvan Pty Ltd
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Jirvan Pty Ltd nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package com.jirvan.util;

public class JdbcConnectionConfig<T> implements ConnectionConfig<T> {

    private String dataSourceOrDriverClassName;
    private String connectString;

    public JdbcConnectionConfig() {
    }

    public JdbcConnectionConfig(String dataSourceOrDriverClassName, String connectString) {
        this.dataSourceOrDriverClassName = dataSourceOrDriverClassName;
        this.connectString = connectString;
    }

    public static JdbcConnectionConfig fromHomeDirectoryConfigFile(String homeDirectoryConfigFile, String connectionName) {
        ExtendedProperties configFileProperties = Io.getHomeDirectoryConfigFileProperties(homeDirectoryConfigFile);
        JdbcConnectionConfig connectionConfig = new JdbcConnectionConfig();
        connectionConfig.setDataSourceOrDriverClassName(configFileProperties.getMandatoryProperty(connectionName + ".connection.class"));
        connectionConfig.setConnectString(configFileProperties.getMandatoryProperty(connectionName + ".connection.connectstring"));
        return connectionConfig;
    }

    public String getDataSourceOrDriverClassName() {
        return dataSourceOrDriverClassName;
    }

    public void setDataSourceOrDriverClassName(String dataSourceOrDriverClassName) {
        this.dataSourceOrDriverClassName = dataSourceOrDriverClassName;
    }

    public String getConnectString() {
        return connectString;
    }

    public void setConnectString(String connectString) {
        this.connectString = connectString;
    }

    public T connect() {
        throw new UnsupportedOperationException("The com.jirvan.util.JdbcConnectionConfig.connect method has not been implemented");
    }

    //    public JdbcDbConnection connect() {
//        return JdbcDbConnection.from(this);
//    }

}
