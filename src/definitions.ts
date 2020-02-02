declare module "@capacitor/core" {
  interface PluginRegistry {
    ContactsPlugin: ContactsPluginPlugin;
  }
}

export interface ContactsPluginPlugin {
  echo(options: { value: string }): Promise<{value: string}>;
}

export interface ContactsResult {
  contacts: {
    name: string;
    phones: string[];
  }[];
}
