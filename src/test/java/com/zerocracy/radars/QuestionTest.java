/**
 * Copyright (c) 2016 Zerocracy
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
package com.zerocracy.radars;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link Question}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public final class QuestionTest {

    /**
     * Parses valid text.
     * @throws Exception If some problem inside
     */
    @Test
    public void parsesValidText() throws Exception {
        final Question question = new Question(
            new XMLDocument(
                this.getClass().getResource("slack/q-project.xml")
            ),
            "role assign ARC yegor256"
        );
        MatcherAssert.assertThat(
            question.matches(),
            Matchers.equalTo(true)
        );
        MatcherAssert.assertThat(
            question.code(),
            Matchers.equalTo("hr.roles.assign")
        );
        MatcherAssert.assertThat(
            question.params().get("role"),
            Matchers.equalTo("ARC")
        );
    }

    /**
     * Parses invalid text and builds help.
     * @throws Exception If some problem inside
     */
    @Test
    public void buildsHelp() throws Exception {
        final Question question = new Question(
            new XMLDocument(
                this.getClass().getResource("slack/q-pmo.xml")
            ),
            "link add"
        );
        MatcherAssert.assertThat(
            question.matches(),
            Matchers.equalTo(false)
        );
        MatcherAssert.assertThat(
            question.help(),
            Matchers.containsString("Option \"rel\" is missing")
        );
    }

    /**
     * Test many links.
     * @throws Exception If some problem inside
     * @checkstyle NestedForDepthCheck (150 lines)
     */
    @Test
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public void parsesManyValidTexts() throws Exception {
        final String[] files = {
            "slack/q-pmo-test.xml",
            "slack/q-profile-test.xml",
            "slack/q-project-test.xml",
        };
        for (final String file : files) {
            final XML test = new XMLDocument(
                this.getClass().getResource(file)
            );
            final XML target = new XMLDocument(
                this.getClass().getResource(test.xpath("/test/@target").get(0))
            );
            for (final XML cmd : test.nodes("/test/cmd")) {
                final Question question = new Question(
                    target, cmd.xpath("text/text()").get(0)
                );
                MatcherAssert.assertThat(
                    question.matches(),
                    Matchers.equalTo(true)
                );
                for (final XML opt : cmd.nodes("opts/opt")) {
                    MatcherAssert.assertThat(
                        question.params(),
                        Matchers.hasEntry(
                            Matchers.equalTo(opt.xpath("name/text()").get(0)),
                            Matchers.equalTo(opt.xpath("value/text()").get(0))
                        )
                    );
                }
            }
        }
    }

}