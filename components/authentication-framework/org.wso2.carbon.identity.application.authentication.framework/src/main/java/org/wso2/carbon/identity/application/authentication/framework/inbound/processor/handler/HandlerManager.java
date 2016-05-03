package org.wso2.carbon.identity.application.authentication.framework.inbound.processor.handler;


import org.wso2.carbon.identity.application.authentication.framework.inbound.FrameworkHandlerStatus;
import org.wso2.carbon.identity.application.authentication.framework.inbound.FrameworkRuntimeException;
import org.wso2.carbon.identity.application.authentication.framework.inbound.IdentityMessageContext;
import org.wso2.carbon.identity.application.authentication.framework.inbound.processor.handler.authentication
        .AuthenticationHandler;
import org.wso2.carbon.identity.application.authentication.framework.inbound.processor.handler.extension
        .AbstractPostHandler;
import org.wso2.carbon.identity.application.authentication.framework.inbound.processor.handler.extension
        .AbstractPreHandler;
import org.wso2.carbon.identity.application.authentication.framework.inbound.processor.handler.extension
        .ExtensionHandlerPoints;
import org.wso2.carbon.identity.application.authentication.framework.inbound.processor.handler.request
        .AbstractRequestHandler;
import org.wso2.carbon.identity.application.authentication.framework.inbound.processor.handler.response
        .AbstractResponseHandler;
import org.wso2.carbon.identity.application.authentication.framework.internal.FrameworkServiceDataHolder;

import java.util.List;

public class HandlerManager {

    private static volatile HandlerManager instance = new HandlerManager();

    private HandlerManager() {

    }

    public static HandlerManager getInstance() {
        return instance;
    }

    public FrameworkHandlerStatus doPreHandle(ExtensionHandlerPoints extensionHandlerPoint,
                                              IdentityMessageContext identityMessageContext)
            throws FrameworkHandlerException {
        List<AbstractPreHandler> abstractPreHandlers =
                FrameworkServiceDataHolder.getInstance().getPreHandler().get(extensionHandlerPoint);
        for (AbstractPreHandler abstractPreHandler : abstractPreHandlers) {
            if (abstractPreHandler.canHandle(identityMessageContext)) {
                FrameworkHandlerStatus handlerStatus = abstractPreHandler.handle(identityMessageContext);
                if (FrameworkHandlerStatus.REDIRECT.equals(handlerStatus)) {
                    return handlerStatus;
                }
            }
        }
        return FrameworkHandlerStatus.CONTINUE;
    }

    public FrameworkHandlerStatus doPostHandle(ExtensionHandlerPoints extensionHandlerPoint,
                                               IdentityMessageContext identityMessageContext)
            throws FrameworkHandlerException {
        List<AbstractPostHandler> abstractPostHandlers =
                FrameworkServiceDataHolder.getInstance().getPostHandler().get(extensionHandlerPoint);
        for (AbstractPostHandler abstractPostHandler : abstractPostHandlers) {
            if (abstractPostHandler.canHandle(identityMessageContext)) {
                FrameworkHandlerStatus handlerStatus = abstractPostHandler.handle(identityMessageContext);
                if (FrameworkHandlerStatus.REDIRECT.equals(handlerStatus)) {
                    return handlerStatus;
                }
            }
        }
        return FrameworkHandlerStatus.CONTINUE;
    }


    public AuthenticationHandler getAuthenticationHandler(IdentityMessageContext messageContext) {
        List<AuthenticationHandler> authenticationHandlers =
                FrameworkServiceDataHolder.getInstance().getAuthenticationHandlers();
        for (AuthenticationHandler authenticationHandler : authenticationHandlers) {
            if (authenticationHandler.canHandle(messageContext)) {
                return authenticationHandler;
            }
        }
        throw FrameworkRuntimeException.error("Cannot find AuthenticationHandler to handle this request");
    }


    public AbstractResponseHandler getResponseHandler(IdentityMessageContext messageContext) {
        List<AbstractResponseHandler> responseBuilderHandlers =
                FrameworkServiceDataHolder.getInstance().getResponseHandlers();
        for (AbstractResponseHandler responseBuilderHandler : responseBuilderHandlers) {
            if (responseBuilderHandler.canHandle(messageContext)) {
                return responseBuilderHandler;
            }
        }
        throw FrameworkRuntimeException.error("Cannot find AbstractResponseHandler to handle this request");
    }


    public AbstractRequestHandler getProtocolRequestHandler(IdentityMessageContext messageContext) {
        List<AbstractRequestHandler> protocolRequestHandlers =
                FrameworkServiceDataHolder.getInstance().getRequestHandlers();
        for (AbstractRequestHandler protocolRequestHandler : protocolRequestHandlers) {
            if (protocolRequestHandler.canHandle(messageContext)) {
                return protocolRequestHandler;
            }
        }
        throw FrameworkRuntimeException.error("Cannot find AbstractRequestHandler to handle this request");
    }
}