import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { SSVC } from 'src/entities';
import { Repository } from 'typeorm';
import axios from 'axios';
import { ConfigService } from '@nestjs/config';
@Injectable()
export class SsvcService {
    private ssvcApi: string;
    constructor(
        @InjectRepository(SSVC)
        private ssvcRepository: Repository<SSVC>,
        private configService: ConfigService,
    ) {
        this.ssvcApi = this.configService.get('SSVC');
    }

    async findOne(cveId: string) {
 
        return await this.callExternalApi(cveId);
          
     
    }

    async callExternalApi(cveId: string) {
            const response = await axios.get(this.ssvcApi, {
                params: {
                    cveId: cveId,
                },
            }).then((response)=>{
                return response.data;
            }).catch((e)=>{
                console.log(e);
                return null;
            });
            return response;
    }
}
