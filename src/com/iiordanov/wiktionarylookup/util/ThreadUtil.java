// ThreadUtil.java
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

package com.iiordanov.wiktionarylookup.util;

import android.os.Handler;

public class ThreadUtil {
	
	/**
	 * Type of an asynchronous task that can fail.
	 *
	 * @param <T1> Data consumed by asynchronous task.
	 * @param <T2> Data produced by asynchronous task.
	 */
	public interface Task<T1, T2> {
		T2 perform (T1 t1) throws Exception;
	}
	
	public interface SuccessHandler<T1> {
		void handle (T1 t1);
	}
	
	public interface FailHandler<T1> {
		void handle (T1 t1, Exception e);
	}
	
	/**
	 * Polymorphic, higher-order function to perform an asynchronous task
	 * with synchronous success/failure callbacks.
	 * 
	 * @param <T1> Type of data consumed by asynchronous task (input).
	 * @param <T2> Type of data produced by asynchronous task (output).
	 * @param t1 Data consumed by asynchronous task.
	 * @param task Asynchronous task.
	 * @param success Success handler gets passed asynchronous task output.
	 * @param fail Failure handler gets passed asynchronous task input and Exception.
	 */
	public static <T1, T2> void
	performTaskAsync (T1 t1, Task<T1, T2> task, SuccessHandler<T2> success, FailHandler<T1> fail) {
		
		final T1                  _t1      = t1;
		final Task<T1, T2>        _task	   = task;
		final FailHandler<T1>     _fail    = fail;
		final SuccessHandler<T2>  _success = success;
		final Handler             cb       = new Handler ();

		final Thread worker = new Thread () {
			public void run () {
				try {
					T2 t2 = _task.perform (_t1);
					cb.post (new SuccessRunnable<T2> (_success, t2));
				} catch (Exception e) {
					cb.post (new FailRunnable<T1> (_fail, _t1, e));
				}
			}
		};
		worker.start ();
	}
	
	/**
	 * Runnable wrapper for a SuccessHandler.
	 *
	 * @param <T1> Type passed to SuccessHandler.
	 */
	private static class SuccessRunnable<T1> implements Runnable {
		T1 t1;
		SuccessHandler<T1> success;
		
		public SuccessRunnable (SuccessHandler<T1> success, T1 t1) {
			this.success = success;
			this.t1 = t1;
		}
		
		public void run () {
			success.handle (t1);
		}
	}
	
	/**
	 * Runnable wrapper for a FailHandler.
	 *
	 * @param <T1> Type passed to FailHandler.
	 */
	private static class FailRunnable<T1> implements Runnable {
		T1 t1;
		Exception e;
		FailHandler<T1> fail;
		
		public FailRunnable (FailHandler<T1> fail, T1 t1, Exception e) {
			this.fail = fail;
			this.t1 = t1;
			this.e = e;
		}
		
		public void run () {
			fail.handle (t1, e);
		}
	}
	
}
