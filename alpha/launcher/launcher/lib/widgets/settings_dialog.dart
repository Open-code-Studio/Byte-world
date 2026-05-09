
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../providers/launcher_provider.dart';

class SettingsDialog extends StatefulWidget {
  const SettingsDialog({super.key});

  @override
  State<SettingsDialog> createState() => _SettingsDialogState();
}

class _SettingsDialogState extends State<SettingsDialog> {
  late GameSettings _settings;
  late String _resolution;
  late bool _fullscreen;
  late String _graphicsQuality;
  late int _renderDistance;
  late bool _vsync;

  final List<String> _resolutions = [
    '800x600',
    '1024x768',
    '1280x720',
    '1920x1080',
    '2560x1440',
    '3840x2160',
  ];

  final List<String> _graphicsQualities = [
    'low',
    'medium',
    'high',
    'ultra',
  ];

  @override
  void initState() {
    super.initState();
    final provider = Provider.of<LauncherProvider>(context, listen: false);
    _settings = provider.settings;
    _resolution = _settings.resolution;
    _fullscreen = _settings.fullscreen;
    _graphicsQuality = _settings.graphicsQuality;
    _renderDistance = _settings.renderDistance;
    _vsync = _settings.vsync;
  }

  void _saveSettings() {
    Provider.of<LauncherProvider>(context, listen: false).updateSettings(
      GameSettings(
        resolution: _resolution,
        fullscreen: _fullscreen,
        graphicsQuality: _graphicsQuality,
        renderDistance: _renderDistance,
        vsync: _vsync,
      ),
    );
    Navigator.of(context).pop();
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Dialog(
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(16),
      ),
      child: Container(
        width: 500,
        padding: const EdgeInsets.all(24),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  'Settings',
                  style: theme.textTheme.headlineSmall,
                ),
                IconButton(
                  icon: const Icon(Icons.close),
                  onPressed: () => Navigator.of(context).pop(),
                ),
              ],
            ),
            
            const SizedBox(height: 24),
            
            TabBar(
              tabs: const [
                Tab(text: 'Graphics'),
                Tab(text: 'Controls'),
                Tab(text: 'Audio'),
              ],
            ),
            
            const SizedBox(height: 16),
            
            Expanded(
              child: TabBarView(
                children: [
                  _buildGraphicsTab(theme),
                  _buildControlsTab(),
                  _buildAudioTab(),
                ],
              ),
            ),
            
            const SizedBox(height: 24),
            
            Row(
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                TextButton(
                  onPressed: () => Navigator.of(context).pop(),
                  child: const Text('Cancel'),
                ),
                const SizedBox(width: 8),
                FilledButton(
                  onPressed: _saveSettings,
                  child: const Text('Save'),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildGraphicsTab(ThemeData theme) {
    return ListView(
      children: [
        _buildSectionTitle('Display'),
        
        _buildDropdown(
          'Resolution',
          _resolutions,
          _resolution,
          (value) => setState(() => _resolution = value!),
        ),
        
        _buildSwitch(
          'Fullscreen',
          _fullscreen,
          (value) => setState(() => _fullscreen = value!),
        ),
        
        _buildSectionTitle('Quality'),
        
        _buildDropdown(
          'Graphics Quality',
          _graphicsQualities,
          _graphicsQuality,
          (value) => setState(() => _graphicsQuality = value!),
        ),
        
        _buildSlider(
          'Render Distance',
          _renderDistance,
          4,
          16,
          (value) => setState(() => _renderDistance = value.round()),
          suffix: ' chunks',
        ),
        
        _buildSwitch(
          'VSync',
          _vsync,
          (value) => setState(() => _vsync = value!),
        ),
      ],
    );
  }

  Widget _buildControlsTab() {
    return const Center(
      child: Text('Controls settings coming soon!'),
    );
  }

  Widget _buildAudioTab() {
    return const Center(
      child: Text('Audio settings coming soon!'),
    );
  }

  Widget _buildSectionTitle(String title) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 12),
      child: Text(
        title,
        style: Theme.of(context).textTheme.titleSmall?.copyWith(
          color: Theme.of(context).colorScheme.onSurfaceVariant,
        ),
      ),
    );
  }

  Widget _buildDropdown(
    String label,
    List<String> items,
    String value,
    void Function(String?) onChanged,
  ) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(label),
          DropdownButton<String>(
            value: value,
            items: items.map((item) => DropdownMenuItem(
              value: item,
              child: Text(item),
            )).toList(),
            onChanged: onChanged,
          ),
        ],
      ),
    );
  }

  Widget _buildSwitch(String label, bool value, void Function(bool?) onChanged) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(label),
          Switch(value: value, onChanged: onChanged),
        ],
      ),
    );
  }

  Widget _buildSlider(
    String label,
    int value,
    int min,
    int max,
    void Function(double) onChanged, {
    String suffix = '',
  }) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Column(
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(label),
              Text('$value$suffix'),
            ],
          ),
          Slider(
            value: value.toDouble(),
            min: min.toDouble(),
            max: max.toDouble(),
            divisions: max - min,
            onChanged: onChanged,
          ),
        ],
      ),
    );
  }
}
