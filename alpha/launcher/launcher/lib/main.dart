
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'providers/launcher_provider.dart';
import 'screens/home_screen.dart';
import 'screens/login_screen.dart';

void main() {
  runApp(
    ChangeNotifierProvider(
      create: (context) => LauncherProvider(),
      child: const ByteWorldLauncher(),
    ),
  );
}

class ByteWorldLauncher extends StatelessWidget {
  const ByteWorldLauncher({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'ByteWorld Launcher',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          seedColor: const Color(0xFF1E88E5),
          brightness: Brightness.dark,
        ),
        useMaterial3: true,
        fontFamily: 'Inter',
      ),
      home: const LauncherHome(),
      debugShowCheckedModeBanner: false,
    );
  }
}

class LauncherHome extends StatelessWidget {
  const LauncherHome({super.key});

  @override
  Widget build(BuildContext context) {
    final launcherProvider = Provider.of<LauncherProvider>(context);
    
    if (launcherProvider.isLoggedIn) {
      return const HomeScreen();
    } else {
      return const LoginScreen();
    }
  }
}
