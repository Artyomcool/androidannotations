/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.test15.decorator;

import org.androidannotations.test15.EmptyActivityWithoutLayout_;
import org.androidannotations.test15.ebean.BeanWithView_;
import org.androidannotations.test15.ebean.SomeBean_;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Collections;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class DecoratorTest {

	@Before
	public void cleanUp() {
		LogDecoratorHandler.logs.clear();
	}

	@Test
	public void voidLogs() {
		EmptyActivityWithoutLayout_ context = new EmptyActivityWithoutLayout_();
		BeanWithLog_ bean = BeanWithLog_.getInstance_(context);
		bean.test1("1", 2);
		assertThat(LogDecoratorHandler.logs).isEqualTo(Collections.singletonList("test1(a1:1,a2:2): null"));
	}

	@Test
	public void voidLogs2() {
		EmptyActivityWithoutLayout_ context = new EmptyActivityWithoutLayout_();
		BeanWithLog_ bean = BeanWithLog_.getInstance_(context);
		bean.test2("1", 2);
		assertThat(LogDecoratorHandler.logs).isEqualTo(Collections.singletonList("test2(a1:1,a2:2): abc"));
	}

}
