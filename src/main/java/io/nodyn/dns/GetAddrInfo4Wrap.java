/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nodyn.dns;

import io.nodyn.CallbackResult;
import io.nodyn.NodeProcess;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Bob McWhirter
 */
public class GetAddrInfo4Wrap extends AbstractQueryWrap {


    public GetAddrInfo4Wrap(NodeProcess process, String name) {
        super(process, name);
    }

    @Override
    public void start() {
        if (this.name.equals("localhost")) {
            process.getEventLoop().getEventLoopGroup().submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        boolean found = false;
                        InetAddress[] addrs = InetAddress.getAllByName(name);
                        for ( int i = 0 ; i < addrs.length ; ++i ) {
                            if ( addrs[i] instanceof Inet4Address ) {
                                emit("complete", CallbackResult.createSuccess(addrs[i]));
                                found = true;
                                break;
                            }
                        }
                        if ( ! found ) {
                            emit("complete", CallbackResult.createError(new UnknownHostException()));
                        }
                    } catch (UnknownHostException e) {
                        emit("complete", CallbackResult.createError(e));
                    }
                }
            });
        } else {
            dnsClient().lookup4(this.name, this.<Inet4Address>handler());
        }
    }

}
