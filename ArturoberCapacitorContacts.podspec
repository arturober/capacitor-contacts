
  Pod::Spec.new do |s|
    s.name = 'ArturoberCapacitorContacts'
    s.version = '0.0.1'
    s.summary = 'A plugin for getting your contacts from the phone'
    s.license = 'MIT'
    s.homepage = 'https://github.com/arturober/capacitor-contacts.git'
    s.author = 'Arturo Bernal'
    s.source = { :git => 'https://github.com/arturober/capacitor-contacts.git', :tag => s.version.to_s }
    s.source_files = 'ios/Plugin/**/*.{swift,h,m,c,cc,mm,cpp}'
    s.ios.deployment_target  = '11.0'
    s.dependency 'Capacitor'
  end