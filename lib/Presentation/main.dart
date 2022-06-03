import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:medfind_flutter/Application/MedicineSearch/medicine_search_bloc.dart';
import 'package:medfind_flutter/Presentation/Screens/Admin/admin_screen.dart';
import 'package:medfind_flutter/Presentation/Screens/MedicineSearch/test_screen.dart';
import 'package:medfind_flutter/Application/Navigation/navigation_bloc.dart';
import 'package:medfind_flutter/Infrastructure/MedicineSearch/DataSource/medicine_search_data_source.dart';
import 'package:medfind_flutter/Infrastructure/MedicineSearch/Repository/medicine_search_repository.dart';
import 'package:medfind_flutter/Presentation/_Shared/index.dart';
import 'package:medfind_flutter/Presentation/_Shared/theme.dart';

import '_Shared/theme.dart';
import '_Shared/routes.dart';

void main() {
  runApp(const MedFindApp());
}

class MedFindApp extends StatelessWidget {
  const MedFindApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    // SizeConfig.initialize(context);
    return MultiBlocProvider(
      providers: [
        BlocProvider<MedicineSearchBloc>(
            create: (BuildContext context) => MedicineSearchBloc(
                MedicineSearchRepository(MedicineSearchDataSource()))),
        BlocProvider<NavigationBloc>(
            create: (BuildContext context) => NavigationBloc()),
      ],
      child: MaterialApp.router(
        routeInformationParser: MedfindRouter.router.routeInformationParser,
        routerDelegate: MedfindRouter.router.routerDelegate,
        debugShowCheckedModeBanner: false,
        title: 'MedFind',
        theme: getAppTheme(),
      ),
    );
  }
}
