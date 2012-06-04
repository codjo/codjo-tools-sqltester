
/* ======================================================================*/
/*   Generation Automatique du GAP pour : 'AP_LOG' */
/* ======================================================================*/

sp_chgattribute 'AP_LOG', 'identity_gap', 1000000
go

/* ======================================================================*/
if exists (select 1 from  sysindexes where  id = object_id('AP_LOG') and identitygap = 1000000 )
    print 'Identity Gap = 1000000 created'
else
    print 'Fail to create Identity Gap = 1000000'
go