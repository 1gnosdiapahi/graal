/*
 * Copyright (c) 2021, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package org.graalvm.compiler.nodes.calc;

import static org.graalvm.compiler.nodeinfo.NodeCycles.CYCLES_32;
import static org.graalvm.compiler.nodeinfo.NodeSize.SIZE_1;

import org.graalvm.compiler.core.common.type.ArithmeticOpTable.BinaryOp;
import org.graalvm.compiler.core.common.type.IntegerStamp;
import org.graalvm.compiler.debug.GraalError;
import org.graalvm.compiler.graph.IterableNodeType;
import org.graalvm.compiler.graph.NodeClass;
import org.graalvm.compiler.nodeinfo.InputType;
import org.graalvm.compiler.nodeinfo.NodeInfo;
import org.graalvm.compiler.nodes.NodeView;
import org.graalvm.compiler.nodes.StructuredGraph.StageFlag;
import org.graalvm.compiler.nodes.ValueNode;
import org.graalvm.compiler.nodes.extended.GuardedNode;
import org.graalvm.compiler.nodes.extended.GuardingNode;

/**
 * {@link FloatingNode} version of {@link IntegerDivRemNode} if it is known that this node cannot
 * trap nor overflow (either based on the input stamps, or based on the input stamps and an
 * additional guard node.
 */
@NodeInfo(cycles = CYCLES_32, size = SIZE_1)
public abstract class NonTrappingIntegerDivRemNode<OP> extends BinaryArithmeticNode<OP> implements IterableNodeType, GuardedNode {

    @SuppressWarnings("rawtypes") public static final NodeClass<NonTrappingIntegerDivRemNode> TYPE = NodeClass.create(NonTrappingIntegerDivRemNode.class);

    protected NonTrappingIntegerDivRemNode(NodeClass<? extends NonTrappingIntegerDivRemNode<OP>> c, BinaryOp<OP> op, ValueNode x, ValueNode y, GuardingNode guard) {
        super(c, op, x, y);
        this.guard = guard;
    }

    @OptionalInput(InputType.Guard) protected GuardingNode guard;

    @Override
    public GuardingNode getGuard() {
        return guard;
    }

    @Override
    public void setGuard(GuardingNode guard) {
        updateUsagesInterface(this.guard, guard);
        this.guard = guard;
    }

    private boolean canTrap() {
        IntegerStamp yStamp = (IntegerStamp) y.stamp(NodeView.DEFAULT);
        return yStamp.contains(0);
    }

    private boolean canOverflow() {
        return SignedDivNode.divCanOverflow(x, y);
    }

    @Override
    public boolean verify() {
        GraalError.guarantee(!(canTrap() && canOverflow()) || graph().isAfterStage(StageFlag.FIXED_READS), "Floating irem must never trap");
        return super.verify();
    }
}
