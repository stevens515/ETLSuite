package com.cenrise.vfs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.safehaus.uuid.UUID;
import org.safehaus.uuid.UUIDGenerator;

public class UUIDUtil {

	private static Log log = LogFactory.getLog(UUIDUtil.class);

	static boolean nativeInitialized = false;

	static UUIDGenerator ug;

	static org.safehaus.uuid.EthernetAddress eAddr;

	static {
		// Try loading the EthernetAddress library. If this fails, then fallback
		// to
		// using another method for generating UUID's.
		/*
		 * This is always going to fail at the moment try {
		 * System.loadLibrary("EthernetAddress"); //$NON-NLS-1$
		 * nativeInitialized = true; } catch (Throwable t) { //
		 * log.warn(BaseMessages.getString(PKG,
		 * "UUIDUtil.ERROR_0001_LOADING_ETHERNET_ADDRESS") ); //$NON-NLS-1$
		 * //$NON-NLS-2$ // Ignore for now. }
		 */
		ug = UUIDGenerator.getInstance();
		if (nativeInitialized) {
			try {
				com.ccg.net.ethernet.EthernetAddress ea = com.ccg.net.ethernet.EthernetAddress
						.getPrimaryAdapter();
				eAddr = new org.safehaus.uuid.EthernetAddress(ea.getBytes());
			} catch (Exception ex) {
				log.error("无法获得以太网物理地址，返回空操作或生成地址", ex); //$NON-NLS-1$
			} catch (UnsatisfiedLinkError ule) {
				log.error("无法获得以太网物理地址，返回空操作或生成地址", ule); //$NON-NLS-1$
				nativeInitialized = false;
			}
		}

		/*
		 * Add support for running in clustered environments. In this way, the
		 * MAC address of the running server can be added to the environment
		 * with a -DMAC_ADDRESS=00:50:56:C0:00:01
		 */
		if (eAddr == null) {
			String macAddr = System.getProperty("MAC_ADDRESS"); //$NON-NLS-1$
			if (macAddr != null) {
				// On Windows machines, people would be inclined to get the MAC
				// address with ipconfig /all. The format of this would be
				// something like 00-50-56-C0-00-08. So, replace '-' with ':'
				// before
				// creating the address.
				//
				macAddr = macAddr.replace('-', ':');
				eAddr = new org.safehaus.uuid.EthernetAddress(macAddr);
			}
		}

		if (eAddr == null) {
			// Still don't have an Ethernet Address - generate a dummy one.
			eAddr = ug.getDummyAddress();
		}

		// Generate a UUID to make sure everything is running OK.
		UUID olduuId = ug.generateTimeBasedUUID(eAddr);
		if (olduuId == null) {
			log.error("不能生成UUID."); //$NON-NLS-1$
		}

	}

	public static String getUUIDAsString() {
		return getUUID().toString();
	}

	public static UUID getUUID() {
		return ug.generateTimeBasedUUID(eAddr);
	}

}
