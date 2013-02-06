/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp.mp.message;

import java.io.IOException;
import mountainrangepvp.mp.Proxy;

/**
 *
 * @author lachlan
 */
public interface MessageListener {

    public void accept(Message message, Proxy proxy) throws IOException;
}
