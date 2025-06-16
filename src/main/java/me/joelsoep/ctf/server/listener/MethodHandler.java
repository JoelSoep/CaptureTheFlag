package me.joelsoep.ctf.server.listener;

import java.lang.reflect.Method;

public record MethodHandler(Method method, ServerListener instance) {}
