package com.cenrise.step;

import java.util.List;

import com.cenrise.excelinput.StepInjectionMetaEntry;
import com.cenrise.exception.BaseException;

/**
 * This interface allows an external program to inject metadata using a standard
 * flat set of metadata attributes.
 * 
 *
 */
public interface StepMetaInjectionInterface {
	/**
	 * @return A list of step injection metadata entries. In case the data type
	 *         of the entry is NONE (0) you will get at least one entry in the
	 *         details section. You can use this list
	 */
	public List<StepInjectionMetaEntry> getStepInjectionMetadataEntries()
			throws BaseException;

	public void injectStepMetadataEntries(List<StepInjectionMetaEntry> metadata)
			throws BaseException;
}
