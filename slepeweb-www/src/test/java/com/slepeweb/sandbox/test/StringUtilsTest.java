package com.slepeweb.sandbox.test;

import static org.junit.Assert.assertEquals;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class StringUtilsTest {

	@SuppressWarnings("unused")
	private static final Object[] getExamples() {
		return new Object[] {
				new Object[] {"fred and john", "nhoj dna derf"},
				new Object[] {"john", "nhoj"}
		};
	}
	
	@Test
	@Parameters(method="getExamples")
	public void correctlyReverseString(String example, String expectedResult) {
		assertEquals(expectedResult, StringUtils.reverse(example));
	}
}
