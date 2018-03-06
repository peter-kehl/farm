/**
 * Copyright (c) 2016-2018 Zerocracy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to read
 * the Software only. Permissions is hereby NOT GRANTED to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.zerocracy.stk.pm.qa

import com.jcabi.xml.XML
import com.zerocracy.Par
import com.zerocracy.Policy
import com.zerocracy.Project
import com.zerocracy.SoftException
import com.zerocracy.farm.Assume
import com.zerocracy.pm.ClaimIn
import com.zerocracy.pm.ClaimOut
import com.zerocracy.pm.qa.Reviews
import com.zerocracy.pm.staff.Roles

def exec(Project project, XML xml) {
  new Assume(project, xml).notPmo()
  new Assume(project, xml).type('Complete QA review')
  ClaimIn claim = new ClaimIn(xml)
  String job = claim.param('job')
  String inspector = claim.author()
  Roles roles = new Roles(project).bootstrap()
  if (!roles.hasRole(inspector, 'QA', 'ARC')) {
    throw new SoftException(
      new Par('You need to have either QA or ARC roles to do that').say()
    )
  }
  String quality = claim.param('quality')
  if (quality !=~ 'bad|good|acceptable') {
    throw new SoftException(
      new Par('I didn\'t understand what is \"%\"').say(quality)
    )
  }
  Reviews reviews = new Reviews(project).bootstrap()
  if (!reviews.exists(job)) {
    throw new SoftException(
      new Par('Thanks, but QA review is not required in this job').say()
    )
  }
  ClaimOut out = reviews.remove(job, quality == 'good', claim.copy())
  if (quality == 'bad') {
    claim.copy()
      .type('Notify job')
      .param('message', new Par('Quality is low, no payment, see §31').say())
      .postTo(project)
  } else {
    out.type('Make payment')
      .param('reason', new Par('Order was finished, quality is "%s"').say(quality))
      .postTo(project)
  }
  claim.copy()
    .type('Make payment')
    .param('login', inspector)
    .param('reason', 'QA review completed')
    .param('minutes', new Policy().get('30.price', 8))
    .postTo(project)
}
