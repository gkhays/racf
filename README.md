# Simple RACF Record Parser

The [RACF database unload utility](https://www.ibm.com/support/knowledgecenter/#!/SSLTBW_1.13.0/com.ibm.zos.r13.icha300/unload.htm%23unload) produces a fixed position record file that may be processed to extract various attributes.

### IRRDBU00 record types

Each record has a [well defined record type](https://www.ibm.com/support/knowledgecenter/#!/SSLTBW_1.13.0/com.ibm.zos.r13.icha300/ichza3c030.htm%23wq1185). Group types include the following.

| Record Type | Record Name             |
| ----------- | ----------------------- |
| 0100        | Group Basic Data        |
| 0101        | Group Subgroups         |
| 0102        | Group Members           |
| 0103        | Group Installation Data |

There are also user types, of which the following table is a sample.

| Record Type | Record Name             |
| ----------- | ----------------------- |
| 0200        | User Basic Data         |
| 0201        | User Categories         |
| 0202        | User Classes            |
| 0203        | User Group Connections  |
| 0204        | User Installation Data  |
| 0205        | User Connect Data       |

# Other Implementations

[Perl script](https://github.com/s1th/racf) to parse a RACF database file as output by the IRRDBU00 command.
