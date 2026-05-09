
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../providers/launcher_provider.dart';
import '../widgets/version_card.dart';
import '../widgets/settings_dialog.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  @override
  void initState() {
    super.initState();
    Provider.of<LauncherProvider>(context, listen: false).loadVersions();
  }

  @override
  Widget build(BuildContext context) {
    final launcherProvider = Provider.of<LauncherProvider>(context);
    final theme = Theme.of(context);

    return Scaffold(
      appBar: AppBar(
        title: const Text('ByteWorld Launcher'),
        actions: [
          IconButton(
            icon: const Icon(Icons.settings),
            onPressed: () => showDialog(
              context: context,
              builder: (context) => const SettingsDialog(),
            ),
          ),
          PopupMenuButton(
            icon: const Icon(Icons.account_circle),
            itemBuilder: (context) => [
              PopupMenuItem(
                child: const Text('Profile'),
                onTap: () {},
              ),
              PopupMenuItem(
                child: const Text('Logout'),
                onTap: () {
                  launcherProvider.logout();
                },
              ),
            ],
          ),
        ],
      ),
      body: launcherProvider.isLaunching
          ? _buildLaunchingScreen(launcherProvider)
          : _buildMainContent(launcherProvider, theme),
    );
  }

  Widget _buildLaunchingScreen(LauncherProvider provider) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(32),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(
              Icons.gamepad_rounded,
              size: 64,
              color: Color(0xFF1E88E5),
            ),
            const SizedBox(height: 24),
            Text(
              provider.launchStatus,
              style: Theme.of(context).textTheme.titleLarge,
            ),
            const SizedBox(height: 16),
            LinearProgressIndicator(
              value: provider.launchProgress,
              backgroundColor: Colors.grey[800],
              valueColor: const AlwaysStoppedAnimation(Color(0xFF1E88E5)),
            ),
            const SizedBox(height: 8),
            Text('${(provider.launchProgress * 100).toInt()}%'),
          ],
        ),
      ),
    );
  }

  Widget _buildMainContent(LauncherProvider provider, ThemeData theme) {
    return Column(
      children: [
        Expanded(
          child: ListView(
            padding: const EdgeInsets.all(16),
            children: [
              if (provider.launchStatus.isNotEmpty)
                Card(
                  color: Colors.green[900],
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Text(provider.launchStatus),
                  ),
                ),
              
              const SizedBox(height: 16),
              
              const Text(
                'Select Version',
                style: TextStyle(
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                ),
              ),
              
              const SizedBox(height: 8),
              
              if (provider.versions.isEmpty)
                const Center(child: CircularProgressIndicator())
              else
                ...provider.versions.map((version) => VersionCard(
                  version: version,
                  isSelected: provider.selectedVersion == version.id,
                  onSelect: () => provider.setSelectedVersion(version.id),
                )),
            ],
          ),
        ),
        
        Container(
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: theme.colorScheme.surface,
            border: Border(top: BorderSide(color: theme.colorScheme.outline)),
          ),
          child: Row(
            children: [
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      provider.selectedVersion,
                      style: theme.textTheme.titleMedium,
                    ),
                    Text(
                      'Ready to play',
                      style: theme.textTheme.bodySmall?.copyWith(
                        color: Colors.green,
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(width: 16),
              ElevatedButton(
                onPressed: () => provider.verifyFiles(),
                style: ElevatedButton.styleFrom(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 24,
                    vertical: 12,
                  ),
                ),
                child: const Text('Verify'),
              ),
              const SizedBox(width: 8),
              FilledButton(
                onPressed: () => provider.launchGame(),
                style: FilledButton.styleFrom(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 32,
                    vertical: 12,
                  ),
                ),
                child: const Text('Play'),
              ),
            ],
          ),
        ),
      ],
    );
  }
}
