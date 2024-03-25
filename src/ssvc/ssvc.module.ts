import { Module } from '@nestjs/common';
import { SsvcService } from './ssvc.service';
import { SsvcController } from './ssvc.controller';
import { TypeOrmModule } from '@nestjs/typeorm';
import { SSVC } from 'src/entities';

@Module({
    imports:[TypeOrmModule.forFeature([SSVC])],
    controllers: [SsvcController],
    providers: [SsvcService],
})
export class SsvcModule {}
