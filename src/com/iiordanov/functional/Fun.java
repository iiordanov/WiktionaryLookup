// Fun.java
//
// Wiktionary Lookup is the legal property of its developers. Please refer to the
// COPYRIGHT file distributed with this source distribution.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.iiordanov.functional;

/**
 * 
 * Class containing function types for higher-order programming.
 *
 */
public abstract class Fun {
	
	/**
	 * Function of one variable.
	 *
	 * @param <T1>
	 */
	public interface Of1<T1> {
		T1 call ();
	}
	
	/**
	 * Function of two variables.
	 *
	 * @param <T1>
	 * @param <T2>
	 */
	public interface Of2<T1, T2> {
		T2 call (T1 t1);
	}
	
	/**
	 * Function of three variables.
	 *
	 * @param <T1>
	 * @param <T2>
	 * @param <T3>
	 */
	public interface Of3<T1, T2, T3> {
		T3 call (T1 t1, T2 t2);
	}
	
}