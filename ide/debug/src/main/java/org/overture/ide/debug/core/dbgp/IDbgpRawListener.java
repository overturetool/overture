package org.overture.ide.debug.core.dbgp;

public interface IDbgpRawListener {
	void dbgpPacketReceived(int sessionId, IDbgpRawPacket content);

	void dbgpPacketSent(int sessionId, IDbgpRawPacket content);
}
