package org.toxsoft.skf.bridge.incub;

import org.osgi.framework.*;

@SuppressWarnings( "javadoc" )
public class Activator
    implements BundleActivator {

  private static BundleContext context;

  static BundleContext getContext() {
    return context;
  }

  @Override
  public void start( BundleContext bundleContext )
      throws Exception {
    Activator.context = bundleContext;
  }

  @Override
  public void stop( BundleContext bundleContext )
      throws Exception {
    Activator.context = null;
  }

}
