package com.uranium.inject;

import org.objectweb.asm.*;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class UraniumTransformer implements ClassFileTransformer {
    
    @Override
    public byte[] transform(ClassLoader loader, String className, 
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, 
                            byte[] classfileBuffer) {
        
        if (className == null) return classfileBuffer;
        
        // Inject into Minecraft classes
        if (className.equals("net/minecraft/client/MinecraftClient")) {
            return transformMinecraftClient(classfileBuffer);
        }
        
        return classfileBuffer;
    }
    
    private byte[] transformMinecraftClient(byte[] original) {
        ClassReader cr = new ClassReader(original);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        
        ClassVisitor cv = new ClassVisitor(Opcodes.ASM9, cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, 
                                           String desc, String signature, 
                                           String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                
                // Hook into init method
                if (name.equals("<init>")) {
                    return new MethodVisitor(Opcodes.ASM9, mv) {
                        @Override
                        public void visitInsn(int opcode) {
                            if (opcode == Opcodes.RETURN) {
                                // Inject our client startup
                                mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                                    "com/uranium/UraniumClient",
                                    "inject",
                                    "()V",
                                    false);
                            }
                            super.visitInsn(opcode);
                        }
                    };
                }
                return mv;
            }
        };
        
        cr.accept(cv, 0);
        return cw.toByteArray();
    }
}
