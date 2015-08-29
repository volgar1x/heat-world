/**
 * <p>Messages are immutables, thus avoiding any possible concurrent modifications.
 * <p>There are 3 type of messages :
 * <ul>
 *     <li>requests, sent by a client, may have or may have not responded</li>
 *     <li>responses, sent by a server, responding to a request</li>
 *     <li>notifications, arbitrarily sent by a server</li>
 * </ul>
 */
package org.heat.backend.messages;