/*
 * Copyright (c) 2016, Oracle and/or its affiliates.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oracle.truffle.llvm.tools;

import java.io.File;

import com.oracle.truffle.llvm.tools.util.ProcessUtil;
import com.oracle.truffle.llvm.tools.util.ProcessUtil.ProcessResult;

public final class Mx {

    private Mx() {
    }

    public static ProcessResult execute(String string) {
        String command = "mx " + string;
        return ProcessUtil.executeNativeCommandZeroReturn(command);
    }

    public static File executeGetLLVMProgramPath(String program) {
        ProcessResult result = Mx.execute("su-get-llvm " + program);
        assert result.getReturnValue() == 0;
        File llvmPath = getProgramPathFromStdout(result);
        return llvmPath;
    }

    public static File executeGetGCCProgramPath(String program) {
        ProcessResult result = Mx.execute("su-get-gcc " + program);
        File llvmPath = getProgramPathFromStdout(result);
        assert result.getReturnValue() == 0;
        return llvmPath;
    }

    private static File getProgramPathFromStdout(ProcessResult result) {
        String output = result.getStdOutput();
        File llvmPath = new File(output.trim());
        if (!llvmPath.canExecute()) {
            throw new AssertionError(output + " is not an executable program!");
        }
        return llvmPath;
    }

}
