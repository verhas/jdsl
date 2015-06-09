package com.javax0.jdsl.analyzers;

class Tuples {
	static class LongStringTuple {
		final Long l;
		final String s;

		LongStringTuple(Long l, String s) {
			this.l = l;
			this.s = s;
		}
	}

	static class DoubleStringTuple {
		final Double d;
		final String s;

		DoubleStringTuple(Double d, String s) {
			this.d = d;
			this.s = s;
		}
	}
}
