
import 'package:flutter/foundation.dart';

class LauncherProvider extends ChangeNotifier {
  bool _isLoggedIn = false;
  String _username = '';
  String _selectedVersion = '1.0.0';
  List<GameVersion> _versions = [];
  bool _isLaunching = false;
  double _launchProgress = 0.0;
  String _launchStatus = '';
  
  GameSettings _settings = GameSettings(
    resolution: '1280x720',
    fullscreen: false,
    graphicsQuality: 'medium',
    renderDistance: 8,
    vsync: true,
  );

  bool get isLoggedIn => _isLoggedIn;
  String get username => _username;
  String get selectedVersion => _selectedVersion;
  List<GameVersion> get versions => _versions;
  bool get isLaunching => _isLaunching;
  double get launchProgress => _launchProgress;
  String get launchStatus => _launchStatus;
  GameSettings get settings => _settings;

  Future<void> login(String username, String password) async {
    await Future.delayed(const Duration(seconds: 1));
    _isLoggedIn = true;
    _username = username;
    notifyListeners();
  }

  void logout() {
    _isLoggedIn = false;
    _username = '';
    notifyListeners();
  }

  void setSelectedVersion(String version) {
    _selectedVersion = version;
    notifyListeners();
  }

  Future<void> loadVersions() async {
    await Future.delayed(const Duration(milliseconds: 500));
    _versions = [
      GameVersion(
        id: '1.0.0',
        name: 'ByteWorld 1.0.0',
        type: 'release',
        size: '256 MB',
        releaseDate: '2024-01-15',
      ),
      GameVersion(
        id: '1.0.1',
        name: 'ByteWorld 1.0.1',
        type: 'release',
        size: '260 MB',
        releaseDate: '2024-02-20',
      ),
      GameVersion(
        id: '1.1.0-beta',
        name: 'ByteWorld 1.1.0 Beta',
        type: 'beta',
        size: '275 MB',
        releaseDate: '2024-03-01',
      ),
    ];
    notifyListeners();
  }

  Future<void> launchGame() async {
    _isLaunching = true;
    _launchProgress = 0.0;
    _launchStatus = 'Preparing to launch...';
    notifyListeners();

    await Future.delayed(const Duration(milliseconds: 500));
    _launchProgress = 0.2;
    _launchStatus = 'Verifying game files...';
    notifyListeners();

    await Future.delayed(const Duration(milliseconds: 800));
    _launchProgress = 0.5;
    _launchStatus = 'Loading resources...';
    notifyListeners();

    await Future.delayed(const Duration(milliseconds: 600));
    _launchProgress = 0.8;
    _launchStatus = 'Launching game...';
    notifyListeners();

    await Future.delayed(const Duration(milliseconds: 400));
    _launchProgress = 1.0;
    _launchStatus = 'Game launched!';
    notifyListeners();

    await Future.delayed(const Duration(milliseconds: 500));
    _isLaunching = false;
    notifyListeners();
  }

  void updateSettings(GameSettings newSettings) {
    _settings = newSettings;
    notifyListeners();
  }

  Future<void> verifyFiles() async {
    _launchStatus = 'Verifying game files...';
    _launchProgress = 0.0;
    notifyListeners();

    for (int i = 0; i <= 100; i += 5) {
      await Future.delayed(const Duration(milliseconds: 50));
      _launchProgress = i / 100;
      notifyListeners();
    }

    _launchStatus = 'Verification complete!';
    await Future.delayed(const Duration(seconds: 1));
    _launchStatus = '';
    notifyListeners();
  }
}

class GameVersion {
  final String id;
  final String name;
  final String type;
  final String size;
  final String releaseDate;

  GameVersion({
    required this.id,
    required this.name,
    required this.type,
    required this.size,
    required this.releaseDate,
  });
}

class GameSettings {
  final String resolution;
  final bool fullscreen;
  final String graphicsQuality;
  final int renderDistance;
  final bool vsync;

  GameSettings({
    required this.resolution,
    required this.fullscreen,
    required this.graphicsQuality,
    required this.renderDistance,
    required this.vsync,
  });

  GameSettings copyWith({
    String? resolution,
    bool? fullscreen,
    String? graphicsQuality,
    int? renderDistance,
    bool? vsync,
  }) {
    return GameSettings(
      resolution: resolution ?? this.resolution,
      fullscreen: fullscreen ?? this.fullscreen,
      graphicsQuality: graphicsQuality ?? this.graphicsQuality,
      renderDistance: renderDistance ?? this.renderDistance,
      vsync: vsync ?? this.vsync,
    );
  }
}
