/*   Copyright (C) 2013-2014 Computer Sciences Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. */

package ezbake.profile.cli;

import ezbake.base.thrift.EzSecurityToken;
import ezbake.configuration.EzConfiguration;
import ezbake.configuration.EzConfigurationLoaderException;
import ezbake.profile.EzProfile;
import ezbake.profile.SearchResult;
import ezbake.security.client.EzbakeSecurityClient;
import org.apache.thrift.TException;
import ezbake.thrift.ThriftClientPool;

import java.io.IOException;

/**
 * User: jhastings
 * Date: 5/7/14
 * Time: 8:04 PM
 */
public class EzProfileClient {
    public static void main(String[] args) throws TException, IOException, EzConfigurationLoaderException {
        if (args.length != 2) {
            System.err.println("Must pass firstname lastname");
            throw new RuntimeException();
        }
        EzConfiguration ezConfiguration = new EzConfiguration();

        ThriftClientPool cp = new ThriftClientPool(ezConfiguration.getProperties());
        EzbakeSecurityClient sc = new EzbakeSecurityClient(ezConfiguration.getProperties());

        // Get an EzSecurityToken
        EzSecurityToken tk = sc.fetchTokenForProxiedUser();

        EzProfile.Client client = cp.getClient("EzProfile", EzProfile.Client.class);
        try {
            SearchResult dns = client.searchDnByName(tk, args[0], args[1]);
            System.out.println("Client returned");
            for (String dn : dns.getPrincipals()) {
                System.out.println(dn);
            }
        } finally {
            cp.returnToPool(client);
        }

        cp.close();
        sc.close();
    }
}
