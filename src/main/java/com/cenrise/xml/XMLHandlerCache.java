package com.cenrise.xml;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Singleton to help speed up lookups in an XML DOM tree.<br>
 * The theory is that you often loop over occurrences of a certain tag in a
 * Node.<br>
 * If there are 20 occurrences, you go from index 0..19.<br>
 * Every time we do the following<br>
 * - found node 0<br>
 * - found node 0, 1<br>
 * - found node 0, 1, 2<br>
 * - ...<br>
 * So the time to search node index 19 is 20 times larger on average then index
 * 0.<br>
 * <br>
 * We can solve this by caching the position of index 18 and by starting back at
 * that position.<br>
 * <br>
 * This class is a singleton to keep everyting 100% compatible with the rest of
 * the codebase. <br>
 * 
 */
public class XMLHandlerCache {
	public static final int MAX_NUMBER_OF_ENTRIES = 500;

	private static XMLHandlerCache cache;

	private Hashtable<XMLHandlerCacheEntry, Integer> hashtable;
	private ArrayList<XMLHandlerCacheEntry> list;

	private int cacheHits;

	private XMLHandlerCache() {
		hashtable = new Hashtable<XMLHandlerCacheEntry, Integer>(
				MAX_NUMBER_OF_ENTRIES);
		list = new ArrayList<XMLHandlerCacheEntry>(MAX_NUMBER_OF_ENTRIES);

		cacheHits = 0;
	}

	public synchronized static final XMLHandlerCache getInstance() {
		if (cache != null)
			return cache;

		cache = new XMLHandlerCache();
		return cache;
	}

	/**
	 * Store a cache entry
	 * 
	 * @param entry
	 *            The cache entry to store
	 */
	public synchronized void storeCache(XMLHandlerCacheEntry entry,
			int lastChildNr) {
		hashtable.put(entry, Integer.valueOf(lastChildNr));
		list.add(entry);

		if (list.size() > MAX_NUMBER_OF_ENTRIES) {
			// Simple: the oldest is the first in the list
			XMLHandlerCacheEntry cacheEntry = list.get(0);

			// Remove this one from the cache...
			hashtable.remove(cacheEntry);

			// Remove from the list
			list.remove(0);
		}
	}

	/**
	 * Retrieve the last child were we left off...
	 * 
	 * @param entry
	 *            The cache entry to look for.
	 * @return the last child position or -1 if nothing was found.
	 */
	public int getLastChildNr(XMLHandlerCacheEntry entry) {
		Integer lastChildNr = hashtable.get(entry);
		if (lastChildNr != null) {
			cacheHits++;
			return lastChildNr.intValue();
		}
		return -1;
	}

	/**
	 * @return the number of cache hits for your statistical pleasure.
	 */
	public int getCacheHits() {
		return cacheHits;
	}

	/**
	 * Allows you to (re-)set the number of cache hits
	 * 
	 * @param cacheHits
	 *            the number of cache hits.
	 */
	public void setCacheHits(int cacheHits) {
		this.cacheHits = cacheHits;
	}

	/**
	 * Clears the cache
	 *
	 */
	public synchronized void clear() {
		this.hashtable.clear();
		this.list.clear();
	}
}
