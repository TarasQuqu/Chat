/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Taras
 */
public interface ConnectionСheck {
    void IfConnectionReady(TCPConnection tcpConnection);
    void GetMsg(TCPConnection tcpConnection,String value);
    void IfDisconnect(TCPConnection tcpConnection);
    void IfException(TCPConnection tcpConnection,Exception e);
}
